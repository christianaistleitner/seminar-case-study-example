import {ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, ViewChild} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  messages = Array<string>();
  input: string = "";
  status: string = "";

  constructor(private changeDetector: ChangeDetectorRef, private http: HttpClient) {
  }

  ngOnInit(): void {
    this.messages = [];
    this.getMessages().subscribe({
      next: data => {
        console.log(data);
        this.messages = [data, ...this.messages];
        this.changeDetector.detectChanges()
      },
      error: err => console.error(err)
    });
  }

  getMessages(): Observable<string> {
    return new Observable<string>((observer) => {
      let eventSource = new EventSource("http://localhost:8080/messages/stream?username=root");
      eventSource.onmessage = (event) => {
        console.debug('Received event: ', event);
        observer.next(event.data);
        let obj = JSON.parse(event.data);
        if (obj.type == "MSG") {
          this.http
            .post(
              "http://localhost:8080/messages/ack",
              obj.id,
              {
                params: {
                  username: "root",
                  recipient: obj.author,
                }
              }
            ).subscribe()
        }

      };
      eventSource.onerror = (error) => {
        // readyState === 0 (closed) means the remote source closed the connection,
        // so we can safely treat it as a normal situation. Another way
        // of detecting the end of the stream is to insert a special element
        // in the stream of events, which the client can identify as the last one.
        if(eventSource.readyState === 0) {
          console.log('The stream has been closed by the server.');
          eventSource.close();
          observer.complete();
        } else {
          observer.error('EventSource error: ' + error);
        }
      }
    });
  }

  sendMessage(text: string) {
    console.log("sending message:" + text);
    this.http
      .post(
        "http://localhost:8080/messages/send",
        text,
        {
          params: {
            username: "root",
            recipient: "root"
          }
        }
      )
      .subscribe({
        next: (data) => {
          this.input = ""
          this.status = "Message sent successfully!";
          this.changeDetector.detectChanges()
          console.log(data);
          alert("Message was read by " + data)
        },
        error: (error) => console.log(error),
        complete: () => {
          console.log('complete');
        }
      });
  }

}

import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
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

  _username: string | null | undefined = null;

  constructor(private changeDetector: ChangeDetectorRef, private http: HttpClient) {
  }

  ngOnInit(): void {
    this.messages = [];
  }

  set username(value: string) {
    this._username = value;
    let eventSource = new EventSource("http://localhost:8080/messages/stream?username=" + this._username);
    eventSource.onmessage = (event) => {
      this.messages = [event.data, ...this.messages];
      this.changeDetector.detectChanges()

      let packet = JSON.parse(event.data);
      if (packet.type == "MSG") {
        this.http.post(
          `http://localhost:8080/messages/ack?username=${this._username}&recipient=${packet.author}`,
          packet.id
        ).subscribe()
      }

    };
    eventSource.onerror = (error) => {
      // readyState === 0 (closed) means the remote source closed the connection,
      // so we can safely treat it as a normal situation. Another way
      // of detecting the end of the stream is to insert a special element
      // in the stream of events, which the client can identify as the last one.
      if (eventSource.readyState === 0) {
        console.log('The stream has been closed by the server.');
        eventSource.close();
      } else {
        console.log('EventSource error: ' + error);
      }
    }
  }

  sendMessage(text: string, recipient: string) {
    console.log("sending message:" + text);
    this.http
      .post(
        "http://localhost:8080/messages/send",
        text,
        {
          params: {
            username: this._username!,
            recipient: recipient
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

import { HttpClient } from '@angular/common/http';
import { Component, Injectable, inject } from '@angular/core';
import { Database, listVal, query, ref } from '@angular/fire/database';
import { map } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
@Injectable()
export class AppComponent {
  title = 'admin';
  private database: Database = inject(Database);
  users: Observable<User[] | null>;
  channels: Observable<Channel[]>;
  model = new SlackMessage(new User("", ""), new Channel("", ""), "")

  constructor(private http: HttpClient) {
    this.users = listVal(query(ref(this.database, "nearbyUsers")))
    this.channels = http.post<Channels>("https://slack.com/api/conversations.list?types=public_channel%2C%20private_channel", "token=" + environment.slack.botToken, { headers: { "Content-Type": "application/x-www-form-urlencoded" } }).pipe(map(channels => channels.channels))
  }

  onSubmit() {
    this.http.post("https://slack.com/api/chat.postMessage?channel=" + this.model.channel.id + "&icon_url=" + encodeURI(this.model.user.profilePictureUrl) + "&text=" + encodeURI(this.model.text) + "&username=" + this.model.user.name, "token=" + environment.slack.botToken, { headers: { "Content-Type": "application/x-www-form-urlencoded" } }).subscribe(response => {
      console.log(JSON.stringify(response))
    })
  }
}

export class SlackMessage {

  constructor(
    public user: User,
    public channel: Channel,
    public text: string
  ) { }

}

export class User {

  constructor(
    public name: string,
    public profilePictureUrl: string
  ) { }

}

export interface Channels {
  channels: Channel[]
}

export class Channel {

  constructor(
    public name: string,
    public id: string
  ) { }

}

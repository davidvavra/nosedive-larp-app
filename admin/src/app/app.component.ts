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
  users: User[] = [];
  channels: Observable<Channel[]>;
  model = new SlackMessage(new User("", ""), new Channel("", ""), "")

  constructor(private http: HttpClient) {
    listVal(query(ref(this.database, "nearbyUsers"))).subscribe(users => {
      if (users != null && !this.isSame(users as User[], this.users)) {
        this.users = users as User[]
      }
    })
    this.channels = http.post<Channels>("https://slack.com/api/conversations.list?types=public_channel%2C%20private_channel", "token=" + environment.slack.botToken, { headers: { "Content-Type": "application/x-www-form-urlencoded" } }).pipe(map(channels => channels.channels))
  }

  onSubmit() {
    let url = "https://slack.com/api/chat.postMessage?channel=" + this.model.channel.id + "&icon_url=" + encodeURIComponent(this.model.user.profilePictureUrl) + "&text=" + encodeURIComponent(this.model.text) + "&username=" + this.model.user.name
    console.log("url="+url)
    this.http.post(url, "token=" + environment.slack.botToken, { headers: { "Content-Type": "application/x-www-form-urlencoded" } }).subscribe(response => {
      console.log(JSON.stringify(response))
      this.model.text = ""
    })
  }

  isSame(first: User[], second: User[]): Boolean {
    return first.length === second.length &&
    first.every((element, index) => element.name === second[index].name && element.profilePictureUrl === second[index].profilePictureUrl);
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


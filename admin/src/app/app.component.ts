import { HttpClient } from '@angular/common/http';
import { Component, Injectable, inject } from '@angular/core';
import { Database, listVal, query, ref, push, serverTimestamp } from '@angular/fire/database';
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
  npcs: User[] = [];
  players: User[] = [];
  channels: Observable<Channel[]>;
  state = new State(NO_USER, new Channel("", ""), "", NO_USER, NO_USER, NO_USER, 0.1, 0.1, "")

  constructor(private http: HttpClient) {
    listVal(query(ref(this.database, "nearbyUsers")), { keyField: "id" }).subscribe(users => {
      if (users != null) {
        users.push(NO_USER)
        let npcUsers = (users as User[]).filter(user => user.id.startsWith("_")).sort((a, b) => a.name.localeCompare(b.name))
        let playerUsers = (users as User[]).filter(user => !user.id.startsWith("_")).sort((a, b) => a.name.localeCompare(b.name))
        if (!this.isSame(npcUsers, this.npcs)) {
          this.npcs = npcUsers
        }
        if (!this.isSame(playerUsers, this.players)) {
          this.players = playerUsers
        }
      }
    })
    this.channels = http.post<Channels>("https://slack.com/api/conversations.list?types=public_channel%2C%20private_channel", "token=" + environment.slack.botToken, { headers: { "Content-Type": "application/x-www-form-urlencoded" } }).pipe(map(channels => channels.channels))
  }

  onMessageSubmit() {
    let url = "https://slack.com/api/chat.postMessage?channel=" + this.state.channel.id + "&icon_url=" + encodeURIComponent(this.state.user.profilePictureUrl) + "&text=" + encodeURIComponent(this.state.text) + "&username=" + this.state.user.name
    console.log("url=" + url)
    this.http.post(url, "token=" + environment.slack.botToken, { headers: { "Content-Type": "application/x-www-form-urlencoded" } }).subscribe(response => {
      console.log(JSON.stringify(response))
      this.state.text = ""
    })
  }

  onReportSubmit() {
    push(ref(this.database, "reports"), {
      "reporter1": this.state.reporter1.id,
      "reporter2": this.state.reporter2.id,
      "victim": this.state.victim.id,
      "penalty": this.state.penalty,
      "reward": this.state.reward,
      "reason": this.state.reason,
      "createdAt": serverTimestamp()
    })
  }

  isSame(first: User[], second: User[]): Boolean {
    return first.length === second.length &&
      first.every((element, index) => element.name === second[index].name && element.profilePictureUrl === second[index].profilePictureUrl);
  }
}

export class State {

  constructor(
    public user: User,
    public channel: Channel,
    public text: string,
    public reporter1: User,
    public reporter2: User,
    public victim: User,
    public penalty: number,
    public reward: number,
    public reason: string
  ) { }

}

export class User {

  constructor(
    public id: string,
    public name: string,
    public profilePictureUrl: string
  ) { }

}

let NO_USER = new User("unknown", "-- Nikdo --", "")

export interface Channels {
  channels: Channel[]
}

export class Channel {

  constructor(
    public name: string,
    public id: string
  ) { }

}


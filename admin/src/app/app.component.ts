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
    this.sendSlackMessage(this.state.user, this.state.channel.id, this.state.text)
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
    let feedChannelId = "C06GTM5JJJC" // TODO: change every run!
    let message = (this.state.reporter2 == NO_USER) ?
      "Uživateli " + this.state.victim.name + " bylo sníženo hodnocení o " + this.state.penalty + "\n\nDůvod: " + this.state.reason + "\n\nDěkujeme uživateli " + this.state.reporter1.name + " za reportování, za odměnu bylo zvýšeno hodnocení o " + this.state.reward
    :
      "Uživateli " + this.state.victim.name + " bylo sníženo hodnocení o " + this.state.penalty + "\n\nDůvod: " + this.state.reason + "\n\nDěkujeme uživatelům " + this.state.reporter1.name + " a " + this.state.reporter2.name + "za reportování, za odměnu jim bylo zvýšeno hodnocení o " + this.state.reward / 2
    this.sendSlackMessage(new User("_dive_safety", "Dive Safety", "https://firebasestorage.googleapis.com/v0/b/nosedive-larp.appspot.com/o/profile_pics%2FDive%20Safety.png?alt=media&token=1003e7ad-28fe-4093-b0f2-6cfc96bd2ee9"), feedChannelId, message)
  }

  isSame(first: User[], second: User[]): Boolean {
    return first.length === second.length &&
      first.every((element, index) => element.name === second[index].name && element.profilePictureUrl === second[index].profilePictureUrl);
  }

  sendSlackMessage(user: User, channelId: string, message: string) {
    let url = "https://slack.com/api/chat.postMessage?channel=" + channelId + "&icon_url=" + encodeURIComponent(user.profilePictureUrl) + "&text=" + encodeURIComponent(message) + "&username=" + user.name
    console.log("url=" + url)
    this.http.post(url, "token=" + environment.slack.botToken, { headers: { "Content-Type": "application/x-www-form-urlencoded" } }).subscribe(response => {
      console.log(JSON.stringify(response))
      this.state.text = ""
    })
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


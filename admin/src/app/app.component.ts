import { Component, Query, inject } from '@angular/core';
import { Database, listVal, query, ref } from '@angular/fire/database';
import { Observable } from 'rxjs/internal/Observable';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'admin';
  private database: Database = inject(Database);
  users: Observable<User[] | null>;
  model = new SlackMessage(new User("", ""), "", "")

  constructor() {
    this.users = listVal(query(ref(this.database, "nearbyUsers")))
  }

  onSubmit() {
    console.log(JSON.stringify(this.model))
  }
}

export class SlackMessage {

  constructor(
    public user: User,
    public channelId: string,
    public text: string
  ) { }

}

export class User {

  constructor(
    public name: string,
    public profilePictureUrl: string
  ) { }

}

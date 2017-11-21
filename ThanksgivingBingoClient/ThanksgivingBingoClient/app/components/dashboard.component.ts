import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { User, GameSocketResponse, UINotification, GameRoom } from '../dtos'
import {Application} from "../appservice"

@Component({
    templateUrl: "../../templates/dashboard.template.htm"
})

export class DashboardComponent implements OnInit {
    user: User;
    app: Application = Application.getApp();
    gameName: string;
    creatingGame: boolean = false;
    /*********** Game Lobby Variables **************/
    gameLobbyName: string = "";
    clientID: string;
    visibleLobbies: Array<GameRoom> = [];

    constructor(private route: ActivatedRoute, private router: Router) { }


    ngOnInit() { this.user = this.app.user; this.fetchGameLobbies(); }
    
    fetchGameLobbies() {
        var self = this;
        Application.getOpenLobbies().done((lobbies: any) => {
            self.visibleLobbies.push(self.app.mainGame);
        });
    }

    joinGame() {
        let link = ['game/'];
        this.router.navigate(link);
    }

}

import { Component, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { User, UINotification, Player, BingoBoard, BingoCell, ChatMessage, GameRoom, GameSocketResponse, GameSocketRequest } from '../dtos'
import {ServiceMethods} from "../servicemethods"
import {Application} from "../appservice"

@Component({
    selector: 'boardview',
    templateUrl: '../../templates/boardview.template.htm'
})

export class BoardViewComponent implements OnInit {

    /*********** Game Lobby Variables **************/
    gameLobbyName: string = "";
    gameLobby: GameRoom;
    clientID: string;
    isFull = false;
    app: Application = null;
    user: User = null;
    /* Notification Information */
    notification: UINotification;
    gameWebsocketConnection: WebSocket;

    myPlayer: Player;
    activePlayer: Player = null;
    messageText: string = "";
    activeCell: BingoCell = null;

    note: UINotification = new UINotification();

    constructor(private route: ActivatedRoute, private router: Router, private cd: ChangeDetectorRef) { }

    ngOnInit() {
        this.app = Application.getApp();
        this.user = this.app.user;

        if (this.app.loggedIn) {
                this.joinLobby(); 
        } else { 
            let link = ['/'];
            this.router.navigate(link);
        }
    }

    joinLobby() {
        var self = this;
        Application.connectToGameLobby(this.app.secureID).done((o: any) => {
            self.gameWebsocketConnection = new WebSocket("ws://localhost:8080/api/session/" + self.app.secureID)
            this.createSocketOnMessage(self.gameWebsocketConnection);
            if (!self.gameLobby.chat) self.gameLobby.chat = [];
            self.gameLobby.webSocket = self.gameWebsocketConnection;
            for (var i = 0; i < self.gameLobby.players.length; i++) {
                var player = self.gameLobby.players[i];
                if (player.user.userID == self.app.user.userID) {
                    self.myPlayer = player;
                    self.activePlayer = self.myPlayer;
                    var array = self.gameLobby.players.slice(i == 0 ? 1 : 0, i == 0 ? self.gameLobby.players.length : i - 1);
                    var a2 : Array<Player> = null;
                    if (i < self.gameLobby.players.length - 1 && i != 0) {
                        a2 = self.gameLobby.players.slice(i + 1);
                        self.gameLobby.players = array.concat(a2);
                    } else {
                        self.gameLobby.players = array;
                    }
                }
            }
        }).fail(() => {
            alert("Could Not Join The Server! Log in and try again.");
            let link = ['/'];
            self.router.navigate(link);
        });
        self.gameLobby = this.app.currentGame;
    }

    createSocketOnMessage(socket: WebSocket) {
        var self = this;
        socket.onmessage = (evt: any) => {
            //   self.gameWebsocketConnection.send(JSON.stringify({ acceptedUsers: [evt.data] }));
            var response: GameSocketResponse = JSON.parse(evt.data);
            if (response.notificationBody) {
                self.notification = new UINotification();
                self.notification.body = response.notificationBody;
                self.notification.title = response.notificationTitle;
                self.notification.dangerButtonText = response.dangerButtonText;
                self.notification.noButtonText = response.noButtonText;
                self.notification.notificationID = response.notificationID;
            }

            if (response.deltaPlayers) {
                for (var i = 0; i < response.deltaPlayers.length; i++) {
                    var newPlayer = true;
                    let player = response.deltaPlayers[i];
                    if (player.user.userID == self.user.userID) {
                        if (!self.myPlayer) {
                            self.myPlayer = player;
                        } else {
                            self.myPlayer.bingoCard.board = player.bingoCard.board;
                        }
                        if (self.activePlayer == null) {
                            self.activePlayer = self.myPlayer;
                        }
                        newPlayer = false;
                        self.cd.detectChanges();
                    } else {
                        for (var j = 0; j < self.gameLobby.players.length; j++) {
                            let playerClient = self.gameLobby.players[j];

                            if (player.user.userID == playerClient.user.userID) {
                                self.gameLobby.players[j] = player;
                                newPlayer = false;
                                break;
                            }
                        }
                        if (newPlayer) {
                            self.gameLobby.players.push(player);
                        }
                    }
                }
            }

            if (response.chatMessage) {
                self.gameLobby.chat.push({
                    message: response.chatMessage.message, sender: response.chatMessage.sender ? response.chatMessage.sender : <any> { user: { userName: "SERVER" } },
                    timeStamp: response.chatMessage.timeStamp
                });
                self.cd.detectChanges();
            }
        };
    }

    respond(response: boolean) {
        this.gameWebsocketConnection.send(JSON.stringify({ notificationResponse: response, notificationID: this.notification.notificationID }));
        this.notification = null;
    }

    sendMessage() {
        this.gameWebsocketConnection.send(JSON.stringify({ broadCastMessage: this.messageText }));
        this.messageText = "";
    }

    test() {
        this.cd.detectChanges();
        alert(this);
    }

    saveCell(item: BingoCell) {
        var game = new GameSocketRequest();
        game.editCell = item;
        this.gameWebsocketConnection.send(JSON.stringify(game));
        (<any>$("#viewEditNote")).modal('hide'); //I'm not happy about this either.
    }

    viewNote(item: BingoCell) {
        this.activeCell = item;
        (<any>$("#viewEditNote")).modal('show'); //I'm not happy about this either.
    }
}

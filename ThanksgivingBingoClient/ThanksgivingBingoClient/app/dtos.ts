export class User {
    userID: number = 0;
    userName: string = "";
    firstName: string = "";
    lastName: string = "";
    password: string = "";
    salt: string = "";
    tokenSelector: string = "";
    tokenValidator: string = "";
    isActive: boolean = true;
}

export class Player {
    user: User;
    bingoCard: BingoBoard;
}


export class BingoBoard {
    board: BingoCell[][];
}

export class BingoCell {
    x: number;
    y: number;
    marked: boolean;
    note: string;
    cellContents: string;
}

export class GameRoom {
    gameName: string;
    players: Player[];
    chat: ChatMessage[];

    webSocket: WebSocket; //not serialized by server, but stored in DTO for multi-connection
}

export class ChatMessage {
    sender: Player;
    message: string;
    timeStamp: string;
}

export class GameSocketResponse {

    notificationBody: string;
    notificationTitle: string;
    notificationHTML: string;
    noButtonText: string;
    dangerButtonText: string;
    notificationID: string;

    startTickingNow: number;
    assignedInstanceId: number;

    /*** Game state variables ***/
    tickNumber: number;

    deltaPlayers: Array<Player>;

    chatMessage: ChatMessage;

    ping: boolean;

}

export class GameSocketRequest {

    startGame: boolean;
    endGame: boolean;
    endLobby: boolean;
    broadCastMessage: string;
    leaveLobby: boolean;
    editCell: BingoCell;
    ping: boolean;
    notificationID: string;
    notificationResponse: boolean;
}

export class UINotification {
    title: string;
    body: string;
    customBodyHTML: string;
    dangerButtonText: string;
    noButtonText: string;
    notificationID: string;
}

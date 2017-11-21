import {User, GameRoom, BingoBoard, ChatMessage, Player} from './dtos'
import {ServiceMethods} from "./servicemethods"


export class Application {
    public loggedIn: boolean = false;

    public user: User = new User();
    public secureID: string = "";

    public errorCallback: (text: string) => any;
    public showDialogCallback: (title: string, text: string, customHTML: string, dangerButtonText: string, noButtonText: string) => JQueryDeferred<boolean>;

    currentGame: GameRoom = null;
    mainGame: GameRoom = null;

    public static app: Application = new Application();

    public static getApp() {
        if (Application.app) {
            return Application.app;
        } else {
            Application.app = new Application();
            return Application.app;
        }
    }

    public static logIn(user: User) {
        return ServiceMethods.logIn(user).done((retUser: User) => {
            var app = Application.getApp()
            app.user = retUser;
            app.loggedIn = true;
            //Application.startLongPolling();
            prepDisplayAfterLogin();
        });
    }

    public static handleServerError(error: JQueryXHR) {
        Application.getApp().errorCallback(error.responseText);
    }

    public static submitUserAccountCreationRequest(user: User) : JQueryPromise<User> {
        return ServiceMethods.createUser(user).done((u: User) => {
            Application.getApp().user = u;
        });
    }

    public static getGameSecureID(): JQueryPromise<{ selector: string }> {
        return ServiceMethods.getSecureID().done((id: { selector: string }) => {
            Application.getApp().secureID = id.selector;
        });
    }

    public static connectToGameLobby( clientID: string): JQueryPromise<string> {
        var app = Application.getApp();
        app.currentGame = app.mainGame;
        return ServiceMethods.connectToGameServer(clientID).done(() => {});
    }

    public static getOpenLobbies() {
        return ServiceMethods.getActiveGames().done((g: GameRoom) => { Application.getApp().mainGame = g; });
    }


}

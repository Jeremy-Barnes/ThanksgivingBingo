﻿/// <reference path="../Libraries/typings/jquery/jquery.d.ts" />
/// <reference path="../Libraries/typings/jqueryui/jqueryui.d.ts" />
import {User, GameRoom, Player} from './dtos'
import {Application} from "./appservice"

export class ServiceMethods {
    static baseURL: string = "http://localhost:8080/api/bingo/";
    static selectorValidator: string[];
    static jsessionID: string = null;

    private static doAjax(functionName: string, functionService: string, parameters: any, type: string = "POST"): JQueryPromise<any> {
        var param = parameters != null && parameters.constructor === String ? parameters : JSON.stringify(parameters);
        var pathParams = type == "GET" && parameters != null ? "/" + param : "";
        var settings: JQueryAjaxSettings = {
            url: ServiceMethods.baseURL + functionService + "/" + functionName + pathParams + (ServiceMethods.jsessionID != null ? ";jsessionid=" + ServiceMethods.jsessionID : ""),
            type: type,
            contentType: "application/json",
            xhrFields: {
                withCredentials: true,
                cache: false
            },
            cache: false,
            headers: {
                Cookie: ServiceMethods.jsessionID,
            },
            beforeSend: (xhr: any) => {  
                xhr.setRequestHeader('Cookie', ServiceMethods.jsessionID);
            },
            success: (json, status, args) => {
                if (args.getResponseHeader("SelectorValidator")) {
                    ServiceMethods.selectorValidator = args.getResponseHeader("SelectorValidator").split(":");
                }
                if (args.getResponseHeader("JSESSIONID")) {
                    ServiceMethods.jsessionID = args.getResponseHeader("JSESSIONID");
                }
            },
            error: Application.handleServerError,
            data: type == "POST" ? param : "",
            crossDomain: true,
        };
        return jQuery.ajax(settings);
    }

    public static logIn(user: User): JQueryPromise<User> {
        return ServiceMethods.doAjax("getUserFromLogin", "users", user);
    }

    public static getUserFromToken(selector: string, validator: string): JQueryPromise<User> {
        return ServiceMethods.doAjax("getUserFromToken", "users", { selector: selector, validator: validator });
    }

    public static createUser(userCreateRequest: User): JQueryPromise<User> {
        return ServiceMethods.doAjax("createUser", "users", userCreateRequest);
    }
   
    public static checkUserName(searchTerm: string): JQueryPromise<boolean> {
        return ServiceMethods.doAjax("checkUserName", "meta", searchTerm, "GET");
    }

    /************** Game Stuff **************/
    public static getSecureID(): JQueryPromise<{ selector: string }> {
        return ServiceMethods.doAjax("getSecureID", "games", "", "GET");
    }

    public static getActiveGames(): JQueryPromise<GameRoom> {
        return ServiceMethods.doAjax("getAllActiveGames", "games", "", "GET");
    }

    public static connectToGameServer(clientID: string): JQueryPromise<string> {
        return ServiceMethods.doAjax("connectToMainGameServer", "games", clientID, "GET");
    }
}








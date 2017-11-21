import { Component, Input, OnInit } from '@angular/core';
import { Params, Router } from '@angular/router';
import { User} from '../dtos'
import {Application} from "../appservice"

@Component({
    selector: 'viewport',
    templateUrl: "../../templates/app.template.htm"
})

export class AppComponent implements OnInit {
    user: User;
    app: Application = Application.getApp();
    displayingAlerts: boolean
    searchString: string;
    ngOnInit() { this.user = this.app.user; prepDisplay(); }

    constructor(private router: Router) {
    }

}

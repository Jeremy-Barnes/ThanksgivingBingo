import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { User } from '../dtos'
import {Application} from "../appservice"

@Component({
    templateUrl: '../../templates/accountcreate-basicinfo.template.htm'
})

export class AccountCreateBasicInfoComponent {
    user: User;
    confirmPassword: string;
    passwordsMatch: boolean = true;


    constructor(private router: Router) {}

    ngOnInit() { this.user = Application.getApp().user }

    onChangePassword() {
        if (this.confirmPassword && this.confirmPassword.length > 0) {
            this.passwordsMatch = this.confirmPassword == this.user.password
        }
    }

    onSubmit() {
        var self = this;
        Application.submitUserAccountCreationRequest(this.user).then((u: User) => {
            let link = ['/'];
            self.router.navigate(link);
        }).fail((error: JQueryXHR) => {
            alert("Error text received from server (do something with this later): \n\n" + error.responseText)
        });
        return false;
    }

    
}
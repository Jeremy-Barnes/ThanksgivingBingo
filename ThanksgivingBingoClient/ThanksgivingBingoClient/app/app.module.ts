import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule }   from '@angular/forms';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { ReactiveFormsModule }          from '@angular/forms';

import { AppRoutingModule } from './app.routing'
import { AppComponent }   from './components/app.component';
import { LoginComponent }   from './components/login.component';
import { AccountCreateBasicInfoComponent }   from './components/accountcreate-basicinfo.component';
import { DashboardComponent } from './components/dashboard.component';
import { BoardViewComponent } from './components/boardview.component';

import { ErrorComponent } from './components/error.component'
import { ConfirmDialogComponent } from './components/confirmdialog.component'


@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        AppRoutingModule
    ],
    declarations: [LoginComponent, AppComponent, AccountCreateBasicInfoComponent, DashboardComponent, ErrorComponent, ConfirmDialogComponent, BoardViewComponent],
    bootstrap: [AppComponent],
    providers: [{ provide: LocationStrategy, useClass: HashLocationStrategy }]

})
export class AppModule { }

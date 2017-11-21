import { NgModule }             from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { AccountCreateBasicInfoComponent } from './components/accountcreate-basicinfo.component';
import { BoardViewComponent } from './components/boardview.component';
import { DashboardComponent } from './components/dashboard.component';
import { AppComponent } from './components/app.component';


const appRoutes: Routes = [
    {
        path: '',
        redirectTo: '/home',
        pathMatch: 'full'
    },
    {
        path: 'home',
        component: DashboardComponent
    },
    {
        path: 'signUp',
        component: AccountCreateBasicInfoComponent
    },
    {
        path: 'game',
        component: BoardViewComponent
    },
    {
        path: 'game/:id',
        component: BoardViewComponent
    },
];

@NgModule({
    imports: [RouterModule.forRoot(appRoutes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }
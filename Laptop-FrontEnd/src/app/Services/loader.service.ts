import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoaderService {

  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  show() {
    this.loadingSubject.next(true);
    console.log('i am here loading....... waiting to fetch data')
  }

  hide() {
    this.loadingSubject.next(false);

    console.log('done loading.......  data fetched')
  }
}

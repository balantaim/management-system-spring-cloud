import {TestBed} from '@angular/core/testing';
import {HomeController} from './home.controller';

describe('HomeController', () => {
    let service: HomeController;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(HomeController);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});

import {TestBed} from '@angular/core/testing';
import {SignInController} from './sign-in.controller';

describe('SignInController', () => {
    let service: SignInController;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(SignInController);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});

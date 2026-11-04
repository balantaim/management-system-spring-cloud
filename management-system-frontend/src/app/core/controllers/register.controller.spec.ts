import {TestBed} from '@angular/core/testing';
import {RegisterController} from './register.controller';

describe('RegisterController', () => {
    let service: RegisterController;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(RegisterController);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});

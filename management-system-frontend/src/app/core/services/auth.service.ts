import {Service} from '@angular/core';
import {LoginRequest} from "../models/login-request";
import {RegisterRequest} from "../models/register-request";

@Service()
export class AuthService {

    async login(request: LoginRequest): Promise<boolean> {
        await new Promise((resolve) => setTimeout(resolve, 1600));
        // demo only: this address simulates a failed login
        return request.email !== 'wrong@example.com';
    }

    async register(request: RegisterRequest): Promise<boolean> {
        await new Promise((resolve) => setTimeout(resolve, 1600));
        // demo only: this address simulates "already registered"
        return request.email !== 'taken@example.com';
    }

}

import {Service} from '@angular/core';
import {LoginRequest} from "../models/login-request";

@Service()
export class AuthService {

    async login(request: LoginRequest): Promise<boolean> {
        await new Promise((resolve) => setTimeout(resolve, 600));
        // demo only: this address simulates a failed login
        return request.email !== 'wrong@example.com';
    }

}

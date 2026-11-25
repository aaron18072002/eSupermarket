import type { ApiResponse } from "./api";

export type { ApiResponse };

export interface LoginRequest {
  email: string;
  password: string;
}

export interface UserResponse {
  id: string;
  email: string;
  fullName: string;
  phone: string | null;
  address: string | null;
  avatarUrl: string | null;
  status: string;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserResponse;
}

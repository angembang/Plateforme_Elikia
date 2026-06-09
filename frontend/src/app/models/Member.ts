import { Role } from './Role';

export interface Member {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  createdAt: string;
  membershipNumber: string;
  membershipDate: string;
  status: string;
  image?: string;
  role: Role;
}

import { Roles } from "./roles.model";


export interface UserUpdateRequestDto {
    firstName: string;
    lastName: string;
    shopCode:string;
    userName: string;
    phone: string;
    currentPassword: string;
    newPassword: string;
    role: string;  // Role is of type Roles enum
    userImage: File | null;  // Handling file upload (image)
    userImageUrl: string;  // URL for the uploaded image (if needed)
}

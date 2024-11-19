import { User } from "@/models/user.ts";

export interface Comment {
    user_id?: number;
    article_id?: number;
    created_at?: number;
    content?: string;
    user?:User;
    id: number;
}

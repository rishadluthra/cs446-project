import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument, Schema as MongooseSchema } from 'mongoose';

export type ReviewDocument = HydratedDocument<Review>;

@Schema({ timestamps: true })
export class Review {
    id: string;

    @Prop({ type: MongooseSchema.Types.ObjectId, ref: 'User', required: true })
    creatorId: string;

    @Prop({ type: MongooseSchema.Types.ObjectId, ref: 'User', required: true })
    targetId: string;

    @Prop({ required: true })
    rating: Number;

    @Prop({ required: false })
    review: string;

    createdAt: Date;

}

export const ReviewSchema = SchemaFactory.createForClass(Review);
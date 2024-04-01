import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument, Schema as MongooseSchema } from 'mongoose';

export type ReportDocument = HydratedDocument<Report>;

@Schema({ timestamps: true })
export class Report {
  id: string;

  @Prop({ type: MongooseSchema.Types.ObjectId, ref: 'User', required: true })
  creatorId: string;

  @Prop({ type: MongooseSchema.Types.ObjectId, ref: 'User', required: true })
  targetId: string;

  createdAt: Date;

  updatedAt: Date;
}

export const ReportSchema = SchemaFactory.createForClass(Report);

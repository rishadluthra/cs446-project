import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument, Schema as MongooseSchema } from 'mongoose';

export type BeaconDocument = HydratedDocument<Beacon>;

export enum Tag {
  LABOUR = 'labour',
  TOOLS = 'tools',
  TECH = 'tech',
  SOCIAL = 'social',
}

@Schema({ timestamps: true })
export class Beacon {
  id: string;

  @Prop({ type: MongooseSchema.Types.ObjectId, ref: 'User', required: true })
  creatorId: string;

  @Prop({ required: true })
  title: string;

  @Prop({ required: true })
  description: string;

  @Prop({ required: true, enum: Object.values(Tag) })
  tag: Tag;

  @Prop({
    type: {
      type: String,
      enum: ['Point'],
      default: 'Point',
    },
    coordinates: {
      type: [Number],
      required: true,
    },
  })
  location: {
    type: {
      type: string;
      enum: ['Point'];
      default: 'Point';
    };
    coordinates: [number, number];
  };

  createdAt: Date;

  updatedAt: Date;
}

export const BeaconSchema = SchemaFactory.createForClass(Beacon);

BeaconSchema.index({ location: '2dsphere' });

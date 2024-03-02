import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type BeaconDocument = HydratedDocument<Beacon>;

@Schema({ timestamps: true })
export class Beacon {
  id: string;

  @Prop({ required: true })
  creatorId: string;

  @Prop({ required: true })
  title: string;

  @Prop({ required: true })
  description: string;

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

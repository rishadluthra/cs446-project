import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

import { Beacon, BeaconDocument } from './beacon.schema';
import { CreateBeaconDto, FindBeaconsDto } from './dto';

import { GeogratisService } from '../geogratis/geogratis.service';

@Injectable()
export class BeaconsService {
  constructor(
    @InjectModel(Beacon.name)
    private readonly beaconModel: Model<BeaconDocument>,
    private readonly geoGratisService: GeogratisService,
  ) { }

  async create(
    { postalCode, ...beaconBody }: CreateBeaconDto,
    creatorId: string,
  ): Promise<Beacon> {
    const coordinates =
      await this.geoGratisService.getCoordinatesFromPostalCode(postalCode);

    if (!coordinates) {
      throw new Error('Invalid postal code');
    }

    return this.beaconModel.create({
      location: { type: 'Point', coordinates },
      ...beaconBody,
      creatorId,
    });
  }

  async find({
    latitude,
    longitude,
    maxDistance,
    tags,
  }: FindBeaconsDto): Promise<Beacon[]> {
    return this.beaconModel.find({
      location: {
        $near: {
          $geometry: {
            type: 'Point',
            coordinates: [latitude, longitude],
          },
          $maxDistance: maxDistance,
        },
      },
      ...(tags && tags.length > 0 ? { tag: { $in: tags } } : {}),
    });
  }

  async findByCreatorId(creatorId: string): Promise<Beacon[]> {
    return this.beaconModel.find({ creatorId });
  }

  async update(
    id: string,
    { postalCode, ...beaconBody }: CreateBeaconDto,
    creatorId: string,
  ): Promise<Beacon> {
    const coordinates =
      await this.geoGratisService.getCoordinatesFromPostalCode(postalCode);

    if (!coordinates) {
      throw new Error('Invalid postal code');
    }

    return this.beaconModel.findOneAndUpdate(
      { _id: id, creatorId },
      { location: { type: 'Point', coordinates }, ...beaconBody, creatorId },
      { new: true },
    );
  }

  async delete(id: string, creatorId: string): Promise<Beacon> {
    return this.beaconModel.findOneAndDelete({ _id: id, creatorId });
  }
}

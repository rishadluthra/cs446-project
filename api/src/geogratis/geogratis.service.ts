import { Injectable } from '@nestjs/common';
import axios from 'axios';

@Injectable()
export class GeogratisService {
  private readonly apiClient = axios.create({
    timeout: 5000,
    baseURL: 'https://geogratis.gc.ca/services/geolocation/en',
  });

  async getCoordinatesFromPostalCode(
    postalCode: string,
  ): Promise<[number, number] | null> {
    try {
      const response = await this.apiClient.get(`/locate?q=${postalCode}`);

      const [longitude, latitude] = response.data[0].geometry.coordinates;

      return [latitude, longitude];
    } catch {
      return null;
    }
  }
}

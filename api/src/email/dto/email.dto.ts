import { IsNotEmpty, IsString } from 'class-validator';

export class EmailDto {
  @IsNotEmpty()
  @IsString()
  subject: string;

  @IsNotEmpty()
  @IsString()
  text: string;
}

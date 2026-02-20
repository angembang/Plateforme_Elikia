import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'truncate'
})
export class TruncatePipe implements PipeTransform {

  transform(value: string, limit: number = 80): string {

    if (!value) return '';

    const plainText = value.replaceAll(/<[^>]*>/g, '');

    return plainText.length > limit
      ? plainText.substring(0, limit) + '...'
      : plainText;
  }

}

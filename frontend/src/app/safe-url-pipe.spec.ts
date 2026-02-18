import { SafeUrlPipe } from './safe-url-pipe';
import {DomSanitizer} from '@angular/platform-browser';
import {TestBed} from '@angular/core/testing';

describe('SafeUrlPipe', () => {
    let pipe: SafeUrlPipe;
    let sanitizer: DomSanitizer;

    beforeEach(() => {
      TestBed.configureTestingModule({});

      sanitizer = TestBed.inject(DomSanitizer);
      pipe = new SafeUrlPipe(sanitizer);
    });

    it('should create an instance', () => {
      expect(pipe).toBeTruthy();
    });

    it('should sanitize url', () => {
      const url = 'https://www.youtube.com/embed/test';

      const result = pipe.transform(url);

      expect(result).toBeTruthy();
    });
});

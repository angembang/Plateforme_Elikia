// Generic LogicResult returned by Spring Boot
export interface LogicResult<T> {
  code: string;
  message: string;
  data: T | null;
}

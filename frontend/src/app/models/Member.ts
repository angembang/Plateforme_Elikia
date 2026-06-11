/**
 * Nom du rôle du membre.
 * Utilisé à la place de l'entité Role complète afin de réduire
 * les données exposées par l'API et d'améliorer la sécurité.
 */

 export interface Member {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  createdAt: string;
  membershipNumber: string;
  membershipDate: string;
  status: string;
  image?: string;
  roleName: string;
}

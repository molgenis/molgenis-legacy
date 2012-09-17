/**
 * Package to deal with data in a matrix shaped format, such as phenotypes,
 * genotypes, qtls, and so on.
 * 
 * The modular interfaces allow implementations in various richness, e.g., basic
 * matrix, editable matrix, sortable matrix, filterable matrix. Users of these
 * classes can use 'instanceof' to verify if the matrix they work with actually
 * have this additional capability.
 */
package org.molgenis.matrix;
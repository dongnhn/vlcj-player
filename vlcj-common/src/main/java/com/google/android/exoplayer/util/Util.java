/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Miscellaneous utility functions.
 */
public final class Util {

  private Util() {}

  /**
   * Closes an {@link OutputStream}, suppressing any {@link IOException} that may occur.
   *
   * @param outputStream The {@link OutputStream} to close.
   */
  public static void closeQuietly(OutputStream outputStream) {
    try {
      outputStream.close();
    } catch (IOException e) {
      // Ignore.
    }
  }

  /**
   * Converts text to lower case using {@link Locale#US}.
   *
   * @param text The text to convert.
   * @return The lower case text, or null if {@code text} is null.
   */
  public static String toLowerInvariant(String text) {
    return text == null ? null : text.toLowerCase(Locale.US);
  }

  /**
   * Divides a {@code numerator} by a {@code denominator}, returning the ceiled result.
   *
   * @param numerator The numerator to divide.
   * @param denominator The denominator to divide by.
   * @return The ceiled result of the division.
   */
  public static int ceilDivide(int numerator, int denominator) {
    return (numerator + denominator - 1) / denominator;
  }

  /**
   * Divides a {@code numerator} by a {@code denominator}, returning the ceiled result.
   *
   * @param numerator The numerator to divide.
   * @param denominator The denominator to divide by.
   * @return The ceiled result of the division.
   */
  public static long ceilDivide(long numerator, long denominator) {
    return (numerator + denominator - 1) / denominator;
  }

  /**
   * Returns the index of the largest value in an array that is less than (or optionally equal to)
   * a specified key.
   * <p>
   * The search is performed using a binary search algorithm, and so the array must be sorted.
   *
   * @param a The array to search.
   * @param key The key being searched for.
   * @param inclusive If the key is present in the array, whether to return the corresponding index.
   *     If false then the returned index corresponds to the largest value in the array that is
   *     strictly less than the key.
   * @param stayInBounds If true, then 0 will be returned in the case that the key is smaller than
   *     the smallest value in the array. If false then -1 will be returned.
   */
  public static int binarySearchFloor(long[] a, long key, boolean inclusive, boolean stayInBounds) {
    int index = Arrays.binarySearch(a, key);
    index = index < 0 ? -(index + 2) : (inclusive ? index : (index - 1));
    return stayInBounds ? Math.max(0, index) : index;
  }

  /**
   * Returns the index of the smallest value in an array that is greater than (or optionally equal
   * to) a specified key.
   * <p>
   * The search is performed using a binary search algorithm, and so the array must be sorted.
   *
   * @param a The array to search.
   * @param key The key being searched for.
   * @param inclusive If the key is present in the array, whether to return the corresponding index.
   *     If false then the returned index corresponds to the smallest value in the array that is
   *     strictly greater than the key.
   * @param stayInBounds If true, then {@code (a.length - 1)} will be returned in the case that the
   *     key is greater than the largest value in the array. If false then {@code a.length} will be
   *     returned.
   */
  public static int binarySearchCeil(long[] a, long key, boolean inclusive, boolean stayInBounds) {
    int index = Arrays.binarySearch(a, key);
    index = index < 0 ? ~index : (inclusive ? index : (index + 1));
    return stayInBounds ? Math.min(a.length - 1, index) : index;
  }

  /**
   * Returns the index of the largest value in an list that is less than (or optionally equal to)
   * a specified key.
   * <p>
   * The search is performed using a binary search algorithm, and so the list must be sorted.
   *
   * @param list The list to search.
   * @param key The key being searched for.
   * @param inclusive If the key is present in the list, whether to return the corresponding index.
   *     If false then the returned index corresponds to the largest value in the list that is
   *     strictly less than the key.
   * @param stayInBounds If true, then 0 will be returned in the case that the key is smaller than
   *     the smallest value in the list. If false then -1 will be returned.
   */
  public static<T> int binarySearchFloor(List<? extends Comparable<? super T>> list, T key,
      boolean inclusive, boolean stayInBounds) {
    int index = Collections.binarySearch(list, key);
    index = index < 0 ? -(index + 2) : (inclusive ? index : (index - 1));
    return stayInBounds ? Math.max(0, index) : index;
  }

  /**
   * Returns the index of the smallest value in an list that is greater than (or optionally equal
   * to) a specified key.
   * <p>
   * The search is performed using a binary search algorithm, and so the list must be sorted.
   *
   * @param list The list to search.
   * @param key The key being searched for.
   * @param inclusive If the key is present in the list, whether to return the corresponding index.
   *     If false then the returned index corresponds to the smallest value in the list that is
   *     strictly greater than the key.
   * @param stayInBounds If true, then {@code (list.size() - 1)} will be returned in the case that
   *     the key is greater than the largest value in the list. If false then {@code list.size()}
   *     will be returned.
   */
  public static<T> int binarySearchCeil(List<? extends Comparable<? super T>> list, T key,
      boolean inclusive, boolean stayInBounds) {
    int index = Collections.binarySearch(list, key);
    index = index < 0 ? ~index : (inclusive ? index : (index + 1));
    return stayInBounds ? Math.min(list.size() - 1, index) : index;
  }


  /**
   * Returns the integer equal to the big-endian concatenation of the characters in {@code string}
   * as bytes. {@code string} must contain four or fewer characters.
   */
  public static int getIntegerCodeForString(String string) {
    int length = string.length();
    assert(length <= 4);
    int result = 0;
    for (int i = 0; i < length; i++) {
      result <<= 8;
      result |= string.charAt(i);
    }
    return result;
  }

  /**
   * Returns whether the given character is a carriage return ('\r') or a line feed ('\n').
   *
   * @param c The character.
   * @return Whether the given character is a linebreak.
   */
  public static boolean isLinebreak(int c) {
    return c == '\n' || c == '\r';
  }

}

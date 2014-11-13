public static int argmin(int[] a) {
  int min = Integer.MAX_VALUE;
  int argmin = 0;
  for (int i = 0; i < a.length; ++i) {
    if (a[i] < min) {
      min = a[i];
      argmin = i;
    }
  }
  return argmin;
}
package me.ancastanoev.io;

import java.util.Scanner;

public class InputDevice {
    private static Scanner scanner = new Scanner(System.in);

    public static int nextInt() {
        return scanner.nextInt();
    }

    public static String getLine() throws InputException {
        try{return scanner.nextLine();}
        catch(Exception e){ throw new InputException(e.getMessage());}
    }

    public static int[] getNumbers(int N) {
        int[] a = new int[N];
        for (int i = 0; i < N; i++) {
            a[i] = nextInt();
        }
        return a;
    }
}

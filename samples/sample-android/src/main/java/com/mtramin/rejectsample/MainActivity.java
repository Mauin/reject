/*
 * Copyright 2017 Marvin Ramin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mtramin.rejectsample;

import com.mtramin.reject.Reject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("-- Method calls (void return)--");
        hello();
        sayHi();

        System.out.println("-- Method calls (with return) --");
        int number = getNumber(); // 0 as the method gets rejected
        String string = getString(); // null as the method gets rejected
        SomethingNotRejected smthng = getSomething(); // null as the method gets rejected

        System.out.println("-- Constructors --");
        Something something = new Something();
        try {
            something.saySomething();
        } catch (NullPointerException e) {
            System.out.println("As \"Something\" was Rejected it is now null and throws NPEs.");
        }
    }

    @Reject
    private static SomethingNotRejected getSomething() {
        return new SomethingNotRejected();
    }

    @Reject
    public static int getNumber() {
        return 100;
    }

    @Reject
    public static String getString() {
        return "String";
    }

    @Reject
    private static void hello() {
        System.out.println("Hello");
    }

    @Reject
    public static void sayHi() {
        System.out.println("Hello World!");
    }
}

package com.a9second.bottlerecyclernew.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Created by 9second on 2018/6/12.
 */

public class ArrayUtils {

    /**
     * 查找并替换指定byte数组
     *
     * @param org        of type byte[] 原数组
     * @param search     of type byte[] 要查找的数组
     * @param replace    of type byte[] 要替换的数组
     * @param startIndex of type int 开始搜索索引
     * @return byte[] 返回新的数组
     * @throws UnsupportedEncodingException when
     */

    public static byte[] arrayReplace(byte[] org, byte[] search, byte[] replace, int startIndex) throws UnsupportedEncodingException {

        int index = indexOf(org, search, startIndex);

        if (index != -1) {

            int newLength = org.length + replace.length - search.length;

            byte[] newByte = new byte[newLength];

            System.arraycopy(org, 0, newByte, 0, index);

            System.arraycopy(replace, 0, newByte, index, replace.length);

            System.arraycopy(org, index + search.length, newByte, index + replace.length, org.length - index - search.length);

            int newStart = index + replace.length;

            if ((newByte.length - newStart) > replace.length) {

                return arrayReplace(newByte, search, replace, newStart);

            }

            return newByte;

        } else {

            return org;

        }

    }


    /**
     * 从指定数组的copy一个子数组并返回
     *
     * @param org of type byte[] 原数组
     * @param to  合并一个byte[]
     * @return 合并的数据
     */

    public static byte[] append(byte[] org, byte[] to) {

        byte[] newByte = new byte[org.length + to.length];

        System.arraycopy(org, 0, newByte, 0, org.length);

        System.arraycopy(to, 0, newByte, org.length, to.length);

        return newByte;

    }


    /**
     * 从指定数组的copy一个子数组并返回
     *
     * @param org of type byte[] 原数组
     * @param to  合并一个byte
     * @return 合并的数据
     */

    public static byte[] append(byte[] org, byte to) {

        byte[] newByte = new byte[org.length + 1];

        System.arraycopy(org, 0, newByte, 0, org.length);

        newByte[org.length] = to;

        return newByte;

    }


    /**
     * 从指定数组的copy一个子数组并返回
     *
     * @param org    of type byte[] 原数组
     * @param from   起始点
     * @param append 要合并的数据
     */

    public static void append(byte[] org, int from, byte[] append) {

        System.arraycopy(append, 0, org, from, append.length);

    }


    /**
     * 从指定数组的copy一个子数组并返回
     *
     * @param original of type byte[] 原数组
     * @param from     起始点
     * @param to       结束点
     * @return 返回copy的数组
     */

    public static byte[] copyOfRange(byte[] original, int from, int to) {

        int newLength = to - from;

        if (newLength < 0)

            throw new IllegalArgumentException(from + " > " + to);

        byte[] copy = new byte[newLength];

        System.arraycopy(original, from, copy, 0,

                Math.min(original.length - from, newLength));

        return copy;

    }


    public static byte[] char2byte(String encode, char... chars) {

        Charset cs = Charset.forName(encode);

        CharBuffer cb = CharBuffer.allocate(chars.length);

        cb.put(chars);

        cb.flip();

        ByteBuffer bb = cs.encode(cb);

        return bb.array();

    }


    /**
     * 查找指定数组的起始索引
     *
     * @param org    of type byte[] 原数组
     * @param search of type byte[] 要查找的数组
     * @return int 返回索引
     */

    public static int indexOf(byte[] org, byte[] search) {

        return indexOf(org, search, 0);

    }


    /**
     * 查找指定数组的起始索引
     *
     * @param org        of type byte[] 原数组
     * @param search     of type byte[] 要查找的数组
     * @param startIndex 起始索引
     * @return int 返回索引
     */

    public static int indexOf(byte[] org, byte[] search, int startIndex) {

        KMPUtils kmpMatcher = KMPUtils.getInstance();

        kmpMatcher.computeFailure4Byte(search);

        return kmpMatcher.indexOf(org, startIndex);

//return com.alibaba.common.lang.ArrayUtil.indexOf(org, search);

    }


    /**
     * 查找指定数组的最后一次出现起始索引
     *
     * @param org    of type byte[] 原数组
     * @param search of type byte[] 要查找的数组
     * @return int 返回索引
     */

    public static int lastIndexOf(byte[] org, byte[] search) {

        return lastIndexOf(org, search, 0);

    }


    /**
     * 查找指定数组的最后一次出现起始索引
     *
     * @param org       of type byte[] 原数组
     * @param search    of type byte[] 要查找的数组
     * @param fromIndex 起始索引
     * @return int 返回索引
     */

    public static int lastIndexOf(byte[] org, byte[] search, int fromIndex) {

        KMPUtils kmpMatcher = KMPUtils.getInstance();

        kmpMatcher.computeFailure4Byte(search);

        return kmpMatcher.lastIndexOf(org, fromIndex);

    }

}

package main.cn.vove7.regEngine

import org.junit.Test
import kotlin.test.assertEquals

/**
 * # MainTest
 * Created by 11324.
 * Date: 2019/3/17
 */
class MainTest {
    @Test
    fun customTest() {
        arrayOf(
                Pair("(123)?", listOf(Pair("", true), Pair("123", true), Pair("1234", false))),
                Pair("123|abc", listOf(Pair("123abc", false), Pair("123", true), Pair("abc", true))),
                Pair("[a-z123]*", listOf(Pair("avz", true), Pair("123", true))),
                Pair("12345", listOf(Pair("12345", true), Pair("abc", false))),
                Pair("[abc]*", listOf(Pair("aaa", true), Pair("abc", true), Pair("aabaad", false), Pair("aabaada", false))),
                Pair("%123", listOf(Pair("abc123", true))),
                Pair("[0-9]+", listOf(/*Pair("1323", true),*/ Pair("", false), Pair("e12dd", false))),
                Pair("abc?#", listOf(Pair("abcqw", false), Pair("ab12", true), Pair("abc80", true))),
                Pair("(123)?abc", listOf(Pair("12ab", false), Pair("abc", true))),
                Pair("#%", listOf(Pair("12", true), Pair("12ab", true)))
        ).forEach { item ->
            val regex = item.first.toParamRegex()
            item.second.forEach { iv ->
                val s = iv.first
                val b = regex.match(s)
                if (b != null) {
                    Vog.d(b)
                    assertEquals(iv.second, true, item.first + "\t" + s)
                } else {
                    assertEquals(iv.second, false, item.first + "\t" + s)
                }
            }
            println()
            println()
        }
    }

    @Test
    fun paramTest() {
        arrayOf(
                Pair("(帮我)?@{text}第@{#index}个", listOf(Pair("点击第二个", true),
                        Pair("帮我点击第x个", false), Pair("帮我点击第34个", true)))
                , Pair("@{text}123%", listOf(Pair("你好123", true), Pair("你好来啦", false)))
        ).forEach { item ->
            val regex = item.first.toParamRegex()
            item.second.forEach { iv ->
                val s = iv.first
                val b = regex.match(s)
                if (b != null) {
                    Vog.d(b)
                    assertEquals(iv.second, true, item.first + "\t" + s)
                } else {
                    assertEquals(iv.second, false, item.first + "\t" + s)
                }
            }
            println()
            println()
        }
    }

}


package main.cn.vove7.regEngine

import java.util.*


/**
 * # ParamRegex
 * 带参数的正则解析器
 * Created by 11324.
 * Date: 2019/3/17
 */
class ParamRegex(
        /**
         * 正则式
         */
        private var regex: String) {
    private var regNodeList: List<RegNode>? = null

    //正则式当前解析位置
    var regIndex = 0

    private val groupStack = Stack<RegNode>()

    /**
     * 解析list<RegNode>
     * @return List<RegNode>
     */
    @Throws //异常
    private fun buildRegNodeList(): List<RegNode> {
        val list = mutableListOf<RegNode>()
        val l = regex.length

        val sb = StringBuilder()//收集常量字符

        while (regIndex < l) {
            when (regex[regIndex]) {
                '(' -> {//进入group
                    sb.buildNode(list)//检查前面
                    regIndex++
                    val b = regIndex
                    val group = GroupNode()
                    groupStack.push(group)
                    group.subNodeList = buildRegNodeList()
                    group.regText = regex.substring(b, regIndex)

                    list.add(group)
                }
                ')' -> {//结束
                    val group = groupStack.pop() ?: throw Exception("括号不匹配")
                    regIndex++
                    group.buildMatchCount(regIndex, regex)
                    sb.buildNode(list)

                    return list
                }
                '[' -> {
                    sb.buildNode(list)//检查前面
                    regIndex++
                    val orNode = CharsNode()
                    regIndex = orNode.buildOneCharNodes(regex, regIndex)
                    list.add(orNode)
                }
                '|' -> {//或
                    sb.buildNode(list)//检查前面
                    regIndex++
                    //向后匹配
                    val n = buildRegNodeList()
                    val orNode = OrNode()
                    //连接
                    orNode.orList.add(list.popBack())
                    orNode.orList.add((n as MutableList<RegNode>).popFront())
                    //拼接
                    list.add(orNode)
                    list.addAll(n)
                }
                '@' -> {//参数
                    sb.buildNode(list)//检查前面
                    regIndex++
                    val paramNode = ParamNode()

                    if (regex[regIndex] == '{') {
                        val index = regex.indexOf('}', regIndex + 1)
                        if (index > 0) {
                            paramNode.name = regex.substring(regIndex + 1, index).let {
                                paramNode.regText = "@${it}"
                                if (it[0] == '#') {
                                    paramNode.onlyNumber = true
                                    it.substring(1)
                                } else it
                            }
                            regIndex = index
                            paramNode.minMatchCount = 0
                            paramNode.buildMatchCount(index, regex)
                            list.add(paramNode)
                        } else {
                            throw Exception("@后'}'不匹配 at $regIndex")
                        }
                    } else {
                        throw Exception("@后未跟{ at $regIndex")
                    }
                }
                '#' -> {//数字
                    //无参数 任意匹配
                    sb.buildNode(list)//检查前面
                    list.add(ParamNode().also {
                        it.regText = "#"
                        it.onlyNumber = true
                    })
                }
                '%' -> {
                    sb.buildNode(list)//检查前面
                    list.add(ParamNode().also {
                        it.regText = "%"
                    })
                }
                else -> {//其他字符
                    if (regIndex + 1 < l) {//未超出
                        if (regex[regIndex + 1] in arrayOf('*', '+', '?')) {
                            val singleCharNode = TextNode(regex[regIndex].toString()).apply {
                                regIndex++
                                regIndex = buildMatchCount(regIndex, regex)
                            }
                            val preNode = TextNode(sb.toString())
                            list.add(preNode)
                            list.add(singleCharNode)
                            sb.clear()
                        } else {
                            sb.append(regex[regIndex])
                        }
                    } else {//结尾
                        sb.append(regex[regIndex])
                        sb.buildNode(list)
                    }
                }
            }
            regIndex++
        }

        sb.buildNode(list)
        return list
    }

    /**
     * 匹配
     * @return Map<String, String>? 匹配到的参数，null：失败
     */
    fun match(text: String): Map<String, String>? {

        val matchList = hashMapOf<String, String>()
        var endIndex = 0
        var i = 0
        (regNodeList ?: buildRegNodeList()).apply {
            regNodeList = this

            forEach {
                try {
                    endIndex = (it.match(text, endIndex, this.getOrNull(++i)) ?: return null)
                    Vog.d(" matchValues --> ${it.matchValue}")
                } catch (e: Exception) {
                    Vog.d(e.message)
                    return null
                }
                if (it is ParamNode) {
                    it.name?.also { name ->
                        if (it.onlyNumber)
                            matchList[name] = it.numValue
                        else
                            matchList[name] = it.matchValue
                    }
                }

                if (endIndex >= text.length) {//文本匹配结束
                    return@forEach
                }
            }
        }

        return if (endIndex >= text.length) {
            matchList
        } else null
    }

}

fun StringBuilder.buildNode(list: MutableList<RegNode>) {
    if (isNotEmpty()) {
        val preNode = TextNode(toString())
        list.add(preNode)
    }
    clear()
}


/**************以下为扩展函数***************/


/**
 * 弹出尾部
 * @receiver MutableList<T>
 * @return T
 */
inline fun <reified T> MutableList<T>.popBack(): T {
    val i = get(size - 1)
    removeAt(size - 1)
    return i
}

/**
 * 弹出头部
 * @receiver MutableList<T>
 * @return T
 */
inline fun <reified T> MutableList<T>.popFront(): T {
    val i = get(0)
    removeAt(0)
    return i
}

/**
 * 扩展String类函数
 * @receiver String
 * @return ParamRegex
 */
fun String.toParamRegex(): ParamRegex = ParamRegex(this)
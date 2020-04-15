# 在Android中实现React Hooks

## 1.前言
曾经一段时间人们热衷于在移动端探索图形编程的最佳范式，但由于移动端日渐式微而前端崛起，这类探索也随之转移到了前端。
因此移动端的图形编程架构发展到Google原生提供的残缺MVVM模式后便戛然而止。
而与此同时在Web前端领域，各类框架如雨后春笋般出现，为前端带来了多样的编程范式，
提升开发效率的同时也改变着开发者的认知模型（心智模型）。而其中最具争议性的当属React Hooks API。
React Hooks API 一经面世便引起广泛讨论，经过一段时间沉淀后，开发者态度呈现两极分化，
喜欢者甘之如饴，厌恶者与其划清界限。React所秉承的函数式编程模型，与常用的面向对象/面向过程模型大相径庭。
因此学习曲线略为陡峭，初学时常常感到莫名其妙，但是上手后却又觉得受益无穷，为许多现有难题引入新的解决思路。
本文将探讨如何在Android上实现类似React Hooks 的API，希望能借此来探索移动端图形编程的新方向。

## 2. 实现函数组件
众所周知React有两种创建组件的方法：类组件与函数组件。
类组件可以简化对应到Android里继承View实现的自定义View，函数组件就可以简化类比为一个返回View的函数。
函数组件在React Hooks出现之前一直没有什么存在感，React Hooks可以说给了这个API以灵魂。
那么首先回顾一下React中的创建函数组件的方式：
```javascript
function Welcome(props) {
  return <h1>Hello, {props.name}</h1>;
}
```
可以看到这个函数的入参是该函数组件的属性，可以由外部任意设置；其返回值就是其渲染的具体组件了。
React中引入了JSX来作为UI DSL，所以我们面临的第一个问题就是如何实现Android的动态UI DSL。

### 2.1 UI DSL
为了省去麻烦我直接引入Kotlin团队开发Anko Layouts作为Android的 UI DSL。

[Anko](https://github.com/Kotlin/anko)
> Anko Layouts: a fast and type-safe way to write dynamic Android layouts.

虽然Anko这个库目前已经终止维护了，但是本文只是作探索性研究不需要过于考虑实用性。
再则本文实现并不依赖Anko只是借用其UI DSL功能，后面Jetpack Compose UI出来后也可以用其替换Anko。
甚至你也可以不用UI DSL，直接用new View/addView的方式来写UI也是可行的。

那么有了UI DSL功能之后，就可以在Android中写出类似React的函数组件
```kotlin
fun welcome(props: Map<String, Any>): View {
    return UI {
        textView("Hello, ${props["name"]}")
    }.view
}
```
这样就完成了第一步，在Android中实现函数组件。

## 3. 实现数据绑定
前文提到函数组件在React的早期一直被开发者忽视，这是因为其功能实在太过于简陋，
只能通过设置props来改变组件的表现，组件内部连最基本的**状态**都没有。
而React Hooks首先就给予了函数组件实现内部状态的功能。
在面向对象模型中都是用**变量**来表征对象内部的状态，此处也是类似。
需要注意的是此处的变量是可以和UI进行绑定的，
例如将一个String变量与TextView绑定，那么当该变量值发生改变时TextView所显示的内容也会随之变化。
能和UI进行绑定的变量必须具备两个功能：能感知修改变量值的操作，并能通知到对应的UI组件。
在js中多是覆写Object的set方法来实现这个功能。而在Android中Jetpack中LiveData就提供了这个功能，
并且还针对Android环境添加了感知界面生命周期的功能，十分实用。

接下来再看如何将变量与UI组件进行绑定。
LiveData通过调用observe方法注册onChanged回调来实现通知对方，那么这里就可以将其封装一下，
直接将LivaData对象与View的某个字段或者某个setter方法进行绑定，当数据变化时直接将LivaData的值set到View对象上
```kotlin
 // 绑定View的setter方法
fun <T : Any, U : T> bind(owner: LifecycleOwner, setter: (T) -> Unit,
                          field: NonNullLiveData<U>) =
        field.observe(owner) { setter(it) }
 // 绑定View的成员变量
fun <T : Any, U : T> bind(owner: LifecycleOwner, prop: KMutableProperty0<T>,
                          field: NonNullLiveData<U>) =
        field.observe(owner) { prop.set(it) }
```
这里还有一个变体，当LiveData的数据类型与View的字段不一致时，需要添加一个转换函数将其转为一致的类型
```kotlin
 // 绑定View的setter方法
fun <T : Any, U : Any> bind(owner: LifecycleOwner, setter: (T) -> Unit,
                            field: NonNullLiveData<U>, converter: (U) -> T) =
        field.observe(owner) { setter(converter(it)) }
```
这样就实现了数据绑定，但是仅仅只有数据绑定是不够的。
常用Vue框架或者小程序的开发者应该知道“页面指令”对开发效率的提升有多么巨大。
因此这里还得实现最基础的两个指令——**条件渲染**与**列表渲染**，这样才算是“完全版”的数据绑定。

### 3.1 实现条件渲染
在React中条件渲染直接用if来实现：
```javascript
function Welcome(props) {
  if (!props.name) {
    rettun null;
  }
  return <h1>Hello, {props.name}</h1>;
}
```
这里可以看出React的机制是在props发生变化时每次调用函数重新生成组件。
这与Android的机制不符，若强行按此形式来写会带来一些性能上的损耗。
因此这里可以仿造Vue的条件渲染实现方式：
```html
<!-- 其中awesome与ok均是变量 -->
<h1 v-if="awesome">Vue is awesome!</h1>
<h1 v-show="ok">Hello!</h1>
```
按这种思路来实现条件渲染非常简单，只需要将一个Boolean类型LiveData与View的visibility字段进行绑定即可。
因给View类扩展以下两个方法，就可以实现条件渲染。
```kotlin
fun <T : View> T.bindIf(owner: LifecycleOwner, data: NonNullLiveData<Boolean>): T {
    bind(owner, this::setVisibility, data) { if (it) View.VISIBLE else View.GONE }
    return this
}
fun <T : View> T.bindShow(owner: LifecycleOwner, data: NonNullLiveData<Boolean>): T {
    bind(owner, this::setVisibility, data) { if (it) View.VISIBLE else View.INVISIBLE }
    return this
}
```
注：此处省略v-else与v-else-if指令的实现

### 3.2 实现列表渲染
React中的列表渲染更是直接使用数组这种简单的形式来实现。
```javascript
function NumberList() {
  const numbers = [1, 2, 3, 4, 5];
  return (
    <ul>{
      numbers.map((number) => <li>{number}</li>)
    }</ul>
  );
}
```
众所周知，Android中的列表组件并不是一个View的数组，而是有一套View重用机制来实现。
正因为如此Android才需要用一个adapter来将数据与View绑定。
所以我们没法把列表渲染做到与React完全一致，但是我们可设计一个尽可能相似的API。
此处以RecyclerView为例，要实现列表渲染首先要实现设计一个通用的adapter，
只需要传入一个list就可以根据list的每一项元素生成对应的UI组件。
此处可以设计一个这样的接口```(data, index, viewType) -> View```传入list的一个元素及其索引
（如果该RecyclerView并不是每一项都相同的话，还需要传入该项的viewType值），然后其返回值是列表中该项对应的view组件。
这个接口作为adapter的入参，由使用者自行定义列表每一行要显示的view组件。
完成后的效果如下：
```kotlin
fun simpleWeekList(): View {
    val weekList: LiveList<String> = LiveList(mutableListOf(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    ))

    return UI {
        recyclerView {
            layoutManager = LinearLayoutManager(context)
            adapter = CommonAdapter(owner, weekList) { week, _, _ ->
                UI {
                    textView {
                        bind(owner, ::setText, week)
                    }
                }.view
            }
        }
    }.view
}
```
需要注意的是，这里封装了一个LiveList对象，扩展了LiveData使其能感知list的修改操作。
CommonAdapter中都是Android的样板代码，此处不再累述。
感兴趣的同学可以去[DroidHooks](https://github.com/KalSat/DroidHooks)查看全部代码。

## 4. 实现useState
说了这么多终于可以进入主题。
在已经有了完全版的数据绑定功能功能后，就可开始尝试实现React Hooks的第一个API——useState了。
让我们先看看React中useState的例子：
```javascript
function Example() {
  // 声明一个叫 "count" 的 state 变量
  const [count, setCount] = useState(0);

  return (
    <div>
      <p>You clicked {count} times</p>
      <button onClick={() => setCount(count + 1)}>
        Click me
      </button>
    </div>
  );
}
```
可以看到useState方法返回了一个变量和修改变量的函数，我们可以仿造此形式在Android里实现一个对应的useState
```kotlin
fun <T> useState(initialValue: T): Pair<NonNullLiveData<T>, KFunction1<T, Unit>> {
    val state = NonNullLiveData(initialValue)
    return Pair(state, state::postValue)
}
```
有了这个方法后，可以在Android中实现与上面React一致的例子。
```kotlin
fun counter(props: Map<String, Any>): View {
    // 声明一个叫 "count" 的 state 变量
    val (count, setCount) = useState(0)

    return UI {
        verticalLayout {
            textView {
                bind(owner, ::setText, count) {
                    "You clicked $it times"
                }
            }
            button("Click me") {
                onClick { setCount(count.value + 1) }
            }
        }
    }.view
}
```
由于语言和机制的差异，API与React有少许差异，但是总体思路和使用方式大致是相同的。
至此我们就在Android上实现了第一个Hooks API，为函数组件引入了内部状态。

## 5. 实现useEffect
useEffect这个API为函数组件引入了一个“副作用”的概念，副作用这个名词让人难以理解，
但是简单来说就是执行与组件生命周期相关的操作。
因为函数组件执行的时机可能是页面尚未创建之前，这个时间执行某些操作太早了。
我们常常在页面创建时（生命周期开始）去申请某个资源，而在页面销毁前（生命周期结束）去释放资源，
这类操作在React中被称为“副作用”，useEffect API就是帮助我们来实现这类操作。
还是先来看一个React的例子，这是由上面例子改造而成的计时器，加载到页面上后开始计时每秒加1。
```javascript
function Example() {
  const [count, setCount] = useState(0);

  useEffect(() => {
    const id = setInterval(() => {
      setCount(c => c + 1);
    }, 1000);
    return () => clearInterval(id);
  }, []);

  return <h1>{count}</h1>
}
```
在这个例子中可以看到，useEffect入参函数分为两个部分，函数体是启动定时器，返回值是取消定时器。
函数体部分会在React组件的componentDidMount回调中执行，返回值部分则会在componentWillUnmount回调中执行。
对应到Android的View上，我们可以用attach/detachToWindow回调来模拟。useEffect的实现如下：
```kotlin
 // 将useEffect作为View类的扩展只是为了方便使用
fun <T : View> T.useEffect(create: () -> (() -> Unit)?) {
    onAttachStateChangeListener {
        var destroy: (() -> Unit)? = null
        onViewAttachedToWindow {
            destroy = create()
        }
        onViewDetachedFromWindow {
            destroy?.invoke()
        }
    }
}
```
然后用这个函数在Android中实现上面React计时器的例子：
```kotlin
fun timer(props: Map<String, Any>): View {
    val (count, setCount) = useState(0)

    return UI {
        textView {
            bind(owner, ::setText, count) { "$it" }

            useEffect {
                val timer = timer(period = 1000) {
                    setCount(count.value + 1)
                }
                return@useEffect { timer.cancel() }
            }
        }
    }.view
}
```
这里实现的useEffect与React中的useEffect还是略有差异，React中还有针对componentDidUpdate回调的处理，
这与Android的机制有些差别，此处暂且忽略。

## 6. 总结
那么来回顾一下，本文在Android上模拟实现了React Hooks中最重要的两个API：useState与useEffect。
它们分别为函数组件添加了内部状态与生命周期的能力，将它们组合使用已经可以涵盖大部分普通组件的用例。
函数组件是函数式编程模型在UI编程上的具体体现，其中包含了React中的许多函数式设计思想，例如immutable和stateless等。
文本希望借此形式对Android上的UI编程模型进行一些新思路的探索，希望能给Android开发者带来些许启迪。

完整代码已上传至GitHub：[DroidHooks](https://github.com/KalSat/DroidHooks)  
感谢观看。文中纰漏请各位指正，任何疑问可以留言或私信。

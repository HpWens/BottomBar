# BottomBar

我们活着不能与草木同腐，不能醉生梦死，枉度人生，要有所作为。——方志敏

##概述

我们挑战自我，为用户创造了崭新的视觉设计语言。与此同时，新的设计语言除了遵循经典设计定则，还汲取了最新的科技，秉承了创新的设计理念。这就是原质化设计(Material Design)。同时具有界面更干净、更简约，支持各种新动画效果，具有内置的实时UI阴影，以及可在不同屏幕之间切换的hero元素等特点。

我们先来看看底部导航栏最终效果，有图才有真相嘛：

![md](http://img.blog.csdn.net/20160615001530807)

下面我们一起来看看它的具体实现。

##相关方法

###setColorFilter

`setColorFilter`可以实现滤镜效果。我们可以使用`setColorFilter`实现仿**微信**底部导航栏效果。

方法预览：

```

public final void setColorFilter(int color)
 
public final void setColorFilter(int color, Mode mode)

public void setColorFilter(ColorFilter cf)
```

参数 ：

color      滤镜颜色

mode    混合模式（枚举型）

cf          每一个RGB通道应用转换

`mode`的枚举值：

- `PorterDuff.Mode.CLEAR` 绘制不会提交到画布上
- `PorterDuff.Mode.SRC` 显示上层绘制图片
- `PorterDuff.Mode.DST`  显示下层绘制图片
- `PorterDuff.Mode.SRC_OVER` 正常绘制显示，上下层绘制叠盖。
- `PorterDuff.Mode.DST_OVER`上下层都显示。下层居上显示。
- `PorterDuff.Mode.SRC_IN`  取两层绘制交集。显示上层。
- `PorterDuff.Mode.DST_IN` 取两层绘制交集。显示下层。
- `PorterDuff.Mode.SRC_OUT` 取上层绘制非交集部分。
- `PorterDuff.Mode.DST_OUT` 取下层绘制非交集部分。
- `PorterDuff.Mode.SRC_ATOP` 取下层非交集部分与上层交集部分
- `PorterDuff.Mode.DST_ATOP` 取上层非交集部分与下层交集部分
- `PorterDuff.Mode.XOR` 变暗
- `PorterDuff.Mode.DARKEN` 调亮
- `PorterDuff.Mode.LIGHTEN` 用于颜色滤镜
- `PorterDuff.Mode.MULTIPLY` 
- `PorterDuff.Mode.SCREEN`

`CLEAR`模式：

![md](http://img.blog.csdn.net/20160615100858878)

你对其他模式感兴趣的话，可以写个例子测试下。

`ColorFilter` 参数是对每一个RGB通道应用转换 ，`Android`包含三个`ColorFilter`：

-  `ColorMatrixColorFilter` 可以指定一个`4×5`的`ColorMatrix`并将其应用到一个`Paint`中。`ColorMatrixes`通常在程序中用于对图像进行处理 ，而且由于它们支持使用矩阵相乘的方法来执行链接转换，所以它们很有用。

-  `LightingColorFilter`  乘以第一个颜色的`RGB`通道，然后加上第二个颜色。每一次转换的结果都限制在`0`到`255`之间。

-  `PorterDuffColorFilter`  可以使用数字图像合成的`16`条`Porter-Duff` 规则中的任意一条来向`Paint`应用一个指定的颜色。

```
if (selected) {
    mIcon.setColorFilter(new LightingColorFilter(Color.BLUE, Color.RED));
} else {
    mIcon.setColorFilter(new LightingColorFilter(Color.GRAY, Color.GREEN));
}
```

效果图：

![md](http://img.blog.csdn.net/20160615102035978)

###performClick

主动去调用控件的点击事件（模拟人手去触摸控件）。

举个例子：

xml文件：

```
    <Button
        android:id="@+id/bt"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="模拟人手"/>

    <TextView
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_below="@+id/bt"
        android:layout_marginTop="8dp"
        android:text="触动按钮"/>
```

Activity 文件：

```
 bt = (Button) findViewById(R.id.bt);
 bt.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         Toast.makeText(MainActivity.this, "触摸人手按钮", Toast.LENGTH_LONG).show();
     }
 });
 findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         bt.performClick();
     }
 });
```

效果图：

![md](http://img.blog.csdn.net/20160615103201883)


##BottomBar（底部导航）

了解完了相关方法，接下来分析下底部导航。导航栏是个`ViewGroup`组件，这里使用的是线程布局（LinearLayout）组件，水平排版，每个子布局宽度为`0`，权重为`1`，子布局中包含了`Image`（可以加上一些其他的控件）。接下来我们分别来看一看子布局和父布局。

###BottomBarTab类

`BottomBarTab` 继承的是 `FrameLayout`，由于`BottomBarTab`包含了`Image`,所以在构造方法中把图片资源也添加了进去：

```
public class BottomBarTab extends FrameLayout {

    public BottomBarTab(Context context, @DrawableRes int icon) {
        this(context, null, icon);
    }

    public BottomBarTab(Context context, AttributeSet attrs, @DrawableRes int icon) {
        this(context, attrs, 0,icon);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int defStyleAttr, @DrawableRes int icon) {
        super(context, attrs, defStyleAttr);
        init(context, icon);
    }
```

构造完了，就来看看初始化：

```
private void init(Context context, int icon) {
    TypedArray typedArray = context.obtainStyledAttributes(
            new int[]{R.attr.selectableItemBackgroundBorderless});
    Drawable drawable = typedArray.getDrawable(0);
    setBackgroundDrawable(drawable);
    typedArray.recycle();
    mIcon = new ImageView(context);
    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27, getResources().getDisplayMetrics());
    LayoutParams params = new LayoutParams(size, size);
    params.gravity = Gravity.CENTER;
    mIcon.setImageResource(icon);
    mIcon.setLayoutParams(params);
    mIcon.setColorFilter(Color.parseColor("#c9c9c9"));
    addView(mIcon);
}
```

初始化当中的`applyDimension`方法，参数分别表示：单位，值，和密度。比如这里的值为`27`，我的手机密度为`3`，那么`size`的值`27*3`。

在`BottomBarTab`类中还需要处理选中未选中已经获取当前`position`方法：

```
@Override
public void setSelected(boolean selected) {
    super.setSelected(selected);
    if (selected) {
        mIcon.setColorFilter(new LightingColorFilter(Color.BLUE, Color.RED));
    } else {
        mIcon.setColorFilter(new LightingColorFilter(Color.GRAY, Color.GREEN));
    }
}
public void setTabPosition(int position) {
    mTabPosition = position;
    if (position == 0) {
        setSelected(true);
    }
}
public int getTabPosition() {
    return mTabPosition;
}
```

如果你要新增控件，在`init`方法中添加。`setSelected`选中未选中处理。

###BottomBar类

`BottomBar` 类继承`LinearLayout`为父布局。构造方法：

```
    public BottomBar(Context context) {
        this(context, null);
    }

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
```

初始化方法：

```
    private void init(Context context) {
        setOrientation(VERTICAL);
        mTabLayout = new LinearLayout(context);
        mTabLayout.setBackgroundColor(Color.WHITE);
        mTabLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mTabLayout.setLayoutParams(lp);
        addView(mTabLayout);

        mTabParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mTabParams.weight = 1;
    }
```

初始化方法中设置线性布局的排版方向，新添加了线性组件用于添加子组件`mTabLayout` ，并初始化`mTabLayout`组件的布局参数以及子组件参数（mTabParams ）和权重（weight ）。

有了父组件，那么怎得也有添加子组件的方法了：

```
public BottomBar addItem(final BottomBarTab tab) {
    tab.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = tab.getTabPosition();
            if (mCurrentPosition == position) {
            } else {
                tab.setSelected(true);
                mTabLayout.getChildAt(mCurrentPosition).setSelected(false);
                mCurrentPosition = position;
            }
        }
    });
    tab.setTabPosition(mTabLayout.getChildCount());
    tab.setLayoutParams(mTabParams);
    mTabLayout.addView(tab);
    return this;
}
```

当然最后还有模拟触摸的方法：

```
   /**
     * @param position
     */
    public void setCurrentItem(final int position) {
        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.getChildAt(position).performClick();
            }
        });
    }
```

代码就到这里就告一段落了，源码最后我会附上。

##使用方法

`xml` 布局文件：

```
    <com.ws.bottombar.view.BottomBar
        android:id="@+id/bar"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:layout_alignParentBottom="true">

    </com.ws.bottombar.view.BottomBar>
```

`Activity` 文件：

```

 mBar= (BottomBar) findViewById(R.id.bar);
 mBar.addItem(new BottomBarTab(this,R.mipmap.ic_account_circle_white_24dp)).
         addItem(new BottomBarTab(this,R.mipmap.ic_discover_white_24dp)).
         addItem(new BottomBarTab(this,R.mipmap.ic_arrow_forward_white_24dp)).
         addItem(new BottomBarTab(this,R.mipmap.ic_arrow_back_white_24dp));
         
```

效果图：

![md](http://img.blog.csdn.net/20160615141139572)


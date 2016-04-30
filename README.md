This project showcase how to customize VewGroup to build custom components.

ElasticViewGroup

- Measure
    - If super.onMeasure(widthMeasureSpec, heightMeasureSpec); is called then no need to call setMeasuredDimension() vice-versa.
    - Measure a child
        - child.measure(widthMeasureSpec, heightMeasureSpec);
    - Measure all children with equal dimension
        -  measureChildren(blockSpec, blockSpec);
    - Measure a child with margin

        - If you want to use Margins in ViewGroup then override generateLayoutParams and pass LayoutParams which extends MarginLayoutParams.
        - In onMeasure call measureChildWithMargins()
        - Then mView.getLayoutParams() in onLayout will have margin values passed in xml else those values will be 0.
    - Measure a child with width 100px and height MATCH_PARENT measure spec
        - childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY);
        - childHeightMeasureSpec = MeasureSpec.makeMeasureSpec( MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY);
        - child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
- Layout
    - Layout a child
        - child.layout(newTop, childTop, childRight, childBottom);
- Draw
    - dispatchDraw
        - At this stage child views are already drawn and we can do additional drawing on top like gridlines, divider etc.
    - onDraw
        - This get called before child view and whatever drawn here will be visible under child views.
        - setWillNotDraw(false); to enable onDraw call.
    
![Screenshot](/screenshots/elasticviewgroup.gif)


Resource :
https://www.youtube.com/watch?v=-8M5nDABiqg


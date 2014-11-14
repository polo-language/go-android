getLeft()
getTop()
getRight() and getBottom()
getWidth() and getHeight()

public void setAlpha (float alpha)
for alpha <1, override: public boolean hasOverlappingRendering () to return false


///////////////////////////////////////////////////////////
  public class MyActivity extends Activity implements View.OnTouchListener {

  TextView _view;
  ViewGroup _root;
  private int _xDelta;
  private int _yDelta;

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);

      _root = (ViewGroup)findViewById(R.id.root);

      _view = new TextView(this);
      _view.setText("TextView!!!!!!!!");

      RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
      layoutParams.leftMargin = 50;
      layoutParams.topMargin = 50;
      layoutParams.bottomMargin = -250;
      layoutParams.rightMargin = -250;
      _view.setLayoutParams(layoutParams);

      _view.setOnTouchListener(this);
      _root.addView(_view);
  }

  public boolean onTouch(View view, MotionEvent event) {
      final int X = (int) event.getRawX();
      final int Y = (int) event.getRawY();
      switch (event.getActionMasked()) {
          case MotionEvent.ACTION_DOWN:
              RelativeLayout.LayoutParams lParams =
                  (RelativeLayout.LayoutParams) view.getLayoutParams();
              _xDelta = X - lParams.leftMargin;
              _yDelta = Y - lParams.topMargin;
              break;
          case MotionEvent.ACTION_UP:
              break;
          case MotionEvent.ACTION_POINTER_DOWN:
              break;
          case MotionEvent.ACTION_POINTER_UP:
              break;
          case MotionEvent.ACTION_MOVE:
              RelativeLayout.LayoutParams layoutParams =
                  (RelativeLayout.LayoutParams) view.getLayoutParams();
              layoutParams.leftMargin = X - _xDelta;
              layoutParams.topMargin = Y - _yDelta;
              layoutParams.rightMargin = -250;
              layoutParams.bottomMargin = -250;
              view.setLayoutParams(layoutParams);
              break;
      }
      _root.invalidate();
      return true;
  }
}
///////////////////////////////////////////////////////////
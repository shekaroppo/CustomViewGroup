package shekar.com.customviewgroup.elasticviewgroup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import shekar.com.customviewgroup.R;

public class ElasticActivity extends AppCompatActivity {

    private RecyclerView myRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRv = (RecyclerView) findViewById(R.id.rv);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        myRv.addItemDecoration(new InsetDecoration(this));
        myRv.setLayoutManager(layoutManager);
        myRv.setAdapter(new RecyclerAdapter());
    }
}

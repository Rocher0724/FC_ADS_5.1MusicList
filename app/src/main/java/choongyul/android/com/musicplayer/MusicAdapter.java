package choongyul.android.com.musicplayer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by myPC on 2017-02-01.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder>{
    ArrayList<Music> datas;
    Context context;

    public MusicAdapter(ArrayList<Music> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public MusicAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 이전에는 context 자리에 parent.getContext() 가 들어갔는데 우리는 생성자에 context를 받아왔기 때문에 바로 사용이 가능하다.
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MusicAdapter.Holder holder, int position) {
        // 데이터를 행 단위로 꺼낸다
        final Music music = datas.get(position);

        // 홀더에 데이터를 세팅한다.
        holder.txtTitle.setText(music.getTitle());
        holder.txtArtist.setText(music.getArtist());

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        holder.cardView.setAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTitle,txtArtist;
        CardView cardView;

        public Holder(View v) {
            super(v);

            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            txtArtist = (TextView) v.findViewById(R.id.txtArtist);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            cardView = (CardView) v.findViewById(R.id.cardView);

//            callImgBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = null;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if ( context.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + temp));
//                        }
//                    } else {
//                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + temp));
//                    }
//                    context.startActivity(intent);
//                }
//            });
        }
    }
}

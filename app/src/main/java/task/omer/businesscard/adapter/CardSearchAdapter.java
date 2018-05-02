package task.omer.businesscard.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import task.omer.businesscard.R;
import task.omer.businesscard.model.Person;

public class CardSearchAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Person> personList;
    private ArrayList<Person> arraylist;

    public CardSearchAdapter(Context context, List<Person> personList) {
        this.personList = personList;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(personList);
    }

    public class ViewHolder {
        TextView cardName;
    }

    @Override
    public int getCount() {
        return personList.size();
    }

    @Override
    public Person getItem(int position) {
        return personList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_card, null);

            holder.cardName = view.findViewById(R.id.card_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.cardName.setText(personList.get(position).getFullName());

        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        personList.clear();
        if (charText.length() == 0) {
            personList.addAll(arraylist);
        } else {
            for (Person person : arraylist) {
                if (person.getFullName().toLowerCase().startsWith(charText)) {
                    personList.add(person);
                }
            }
        }
        notifyDataSetChanged();
    }
}
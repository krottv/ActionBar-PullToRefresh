# ActionBar-PullToRefresh

This is fork of ActionBar-PullToRefresh for using in Dialogs or SlidingMenu with ActionBarCompat, if you don't use ActionBarCompat use can easly change little part of the code. The project is used in https://play.google.com/store/apps/details?id=ru.stellio.player


# How to use it

###For usual usage
```java

AbcPullToRefreshAttacher pullToRefreshAttacher;
private void prepaprePullToRefresh() {
       AbcDefaultHeaderTransformer headerTransformer = new AbcDefaultHeaderTransformer();
        //ptr_container is id of layout where need to insert the pullToRefresh
        pullToRefreshAttacher = new AbcPullToRefreshAttacher(this,
                new Options.Builder().headerTransformer(headerTransformer)
                        .headerLayout(R.layout.default_header).build(),(FrameLayout)findViewById(R.id.ptr_container));
    }
    public void addPullToRefresh(OnRefreshListener listener,PullToRefreshLayout layout){
        pullToRefreshAttacher.setOnRefreshListener(listener);
        layout.setPullToRefreshAttacher(pullToRefreshAttacher);
        layout.addAllChildrenAsPullable();
    } 
   ```
###for Dialogs

```java
private void prepaprePullToRefresh() {
       AbcDefaultHeaderTransformer headerTransformer = new AbcDefaultHeaderTransformer();
        //ptr_container is id of layout where need to insert the pullToRefresh
        pullToRefreshAttacher = new AbcPullToRefreshAttacher((ActionBarActivity)getActivity(),
                new Options.Builder().headerTransformer(headerTransformer)
                        .headerLayout(R.layout.dialog_header).build(),
                (FrameLayout)view.findViewById(R.id.ptr_container));
    }
    public void addPullToRefresh(OnRefreshListener listener,PullToRefreshLayout layout){
        pullToRefreshAttacher.setOnRefreshListener(listener);
        layout.setPullToRefreshAttacher(pullToRefreshAttacher);
        layout.addAllChildrenAsPullable();
        }
```
## License

    Copyright 2013 Chris Banes

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

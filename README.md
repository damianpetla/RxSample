# RxSample

This sample demonstrate how to use `replay` function from Rx to cache data and prevent new subscrivers execute background operations from the beginning.

## App structure

It's built with simple MVP pattern. Our focus is on the model side where `Repository` exists. There are 2 views: news list and news details . News list is the one that trigger background operation in the repository. News details is the one that use cached data previously downloaded.

I am using [Kodein](https://github.com/SalomonBrys/Kodein) as dependency injection. The graph is setup inside custom Application class.

## How it works

In `Repository` we initialize a background request that is downloading news from the web.

```kotlin
var request: ConnectableObservable<List<News>>? = null

override fun getNewsList(): Observable<List<News>> {
  if (request == null) {
      val localWithSave = localDataSource.getNewsList()
      val networkWithSave = remoteDataSource.getNewsList().doOnNext {
          localDataSource.storeNewsList(it)
      }
      request = Observable.concat(localWithSave, networkWithSave).first().replay()
      request!!.connect()
  }
  return request!!
}
```

### The flow

1. `Activity` call via presenter `getNewsList()` and register itself as subscriber.
2. `Repositiory` creates new request if `null`.
3. Once request is created I call `request.connect()` to start emmiting items.
4. `Activity` is left and unsubscribe itself from our request but the same request is not stopping.
5. `Activity` is created and subscribe again to `Repository` calling `getNewsList()` via presenter.
6. Now the most interesting part - all chained operations called before `replay()` are not called again. Instead `replay` emits to the subscriber all data previously emitted.
 
### Refresh

In case refreshing by user I simply unsubscribe our request and initialize again.

```kotlin
request?.connect { it.unsubscribe() }
```

## MVP

A comment about how I deal with MVP. I don't really like playing ping pong between `View` and `Presenter`. For me main role of the `Presenter` is to mediate between `View` and `Model`. So if `View` trigger some operation it knows what to do about itself but it's `Presenter's` responsibility to do something with the rest of the components and only with the rest.

### Common use case

1. `View` tell `Presenter` _get me some news_
2. `Presenter` tell `View` _show indicator_
3. `Presenter` tell `Model` _get me some news_
4. `Model` tell `Presenter` _there are your news_
5. `Presenter` tell `View` _hide indicator_
6. `Presenter` tell `View` _use that news list_ or _show error_

I believe that we make `View` very stupid, too much I would say. Since `View` is the one that triggers operation, it knows at which point indicator should be shown or when it should be hidden when we get data from the `Presenter`. 

### My way

So I would keep the flow simpler:

1. `View` tell `Presenter` _get me some news_
2. `Presenter` tell `Model` _get me some news_
3. `Model` tell `Presenter` _there are your news_
4. `Presenter` tell `View` _use that news list_

Of course I am not saying that `Presenter` should never tell `View` to show some indicator. It can, in fact it should but only when `Model` is triggering some actions. For example, asuming that `Presenter` listen to `Model`:

1. `Model` tell `Presenter` _I have some extra news for you_ e.g. from push
2. `Presenter` tell `View` _show some indicator because some data is coming_
3. `Presenter` tell `View` _use that new data_
4. `Presenter` tell `View` _hide indicator, we are done for now_


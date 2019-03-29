// Copyright MyScript. All rights reserved.

package com.moxi.writeNote.myScript;

import android.app.Application;

import com.myscript.iink.Engine;

public class IInkApplication extends Application
{
  private static Engine engine;

  public static synchronized Engine getEngine()
  {
    if (engine == null||engine.isClosed())
    {
      engine = Engine.create(MyCertificate.getBytes());
    }

    return engine;
  }

}

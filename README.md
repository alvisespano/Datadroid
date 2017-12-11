Datadroid v1.0

Written by Alvise Spanò (C) 2017 Universita' Ca' Foscari Venezia


Datadroid is an Android Java library offering wrappers and utilities for several Android components and widely-used APIs, including Google Maps and related components. It is currently under development and its features may change with time.

The library offers a level of abstraction over the typical Android application architecture, transparently performing a number of tasks for dealing with open data downloading, parsing and scraping from several supported sources, allowing the programmer to write code faster and deliver quality apps easily and quickly.

It consists of two distinct modules: a library that can be imported and referenced from a fresh project, accompained by a template app Android Studio project that must be cloned and reused. Certain features no belonging to the library are accessible by reusing such template, which provides several utilities for dealing with map-based activities.

Finally, it includes a prototype sub-package that wraps the core Android API in a strong-typed fashion, using generics and type constraints over type parameters to perform static checks over inter-component communication: code written using such typed wrappers tends to be less bugged, lowering testing time and development time in general.


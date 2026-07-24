require("dotenv").config();

const AnyList = require("anylist");

async function main() {
  const any = new AnyList({
    email: process.env.ANYLIST_EMAIL,
    password: process.env.ANYLIST_PASSWORD,
  });

  await any.login(false);

  const lists = await any.getLists();

  console.log(lists.map(l => l.name));

  console.log(Object.getOwnPropertyNames(
    Object.getPrototypeOf(lists[0])
  ));
}

main();
